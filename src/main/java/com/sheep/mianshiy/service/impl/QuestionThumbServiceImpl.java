package com.sheep.mianshiy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sheep.mianshiy.constant.RedisConstant;
import com.sheep.mianshiy.mapper.QuestionMapper;
import com.sheep.mianshiy.model.entity.Question;
import com.sheep.mianshiy.model.entity.QuestionThumb;
import com.sheep.mianshiy.model.entity.User;
import com.sheep.mianshiy.service.QuestionThumbService;
import com.sheep.mianshiy.mapper.QuestionThumbMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**

*/
@Service
public class QuestionThumbServiceImpl extends ServiceImpl<QuestionThumbMapper, QuestionThumb>
    implements QuestionThumbService{


    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private QuestionMapper questionMapper;


    /**
     * 点赞 / 取消点赞
     * @param questionId
     * @param loginUser
     * @return
     */
    @Override
    public Boolean doQuestionThumb(Long questionId, User loginUser) {
        Long userId = loginUser.getId();
        String redisKey = RedisConstant.USER_THUMB_QUESTION +":" + questionId;
        Long flag = redisTemplate.execute(RedisScript.of(luaScript, Long.class),
                Collections.singletonList(redisKey),
                userId);
        return flag == 1;
    }

    /**
     * 获取点赞数
     * @param questionId
     * @return
     */
    @Override
    public Long getQuestionThumbNum(Long questionId) {
        String redisKey = RedisConstant.USER_THUMB_QUESTION +":" + questionId;
        return redisTemplate.opsForSet().size(redisKey);
    }

    /**
     * 点赞 / 取消点赞
     * @param questionId
     * @param loginUser
     * @return
     */
    @Override
    public Boolean doQuestionThumbImporve(Long questionId, User loginUser) {
        Long userId = loginUser.getId();
        String questionKey = RedisConstant.USER_THUMB_QUESTION + ":" + questionId;
        String userSetKey = RedisConstant.USER_THUMB_USER + ":" + userId;
        // 执行 Lua 脚本
        Long flag = redisTemplate.execute(
                RedisScript.of(script, Long.class),
                Arrays.asList(questionKey,userSetKey),
                userId,questionId);

        return flag == 1;

    }

    /**
     * 查询用户点过赞的题目
     * @param loginUser
     * @return
     */
    @Override
    public List<Question> getUserThumbQuestion(User loginUser) {
        String userSetKey = RedisConstant.USER_THUMB_USER +":" + loginUser.getId();
        Set<Object> questionIdList = redisTemplate.opsForSet().members(userSetKey);
        List<Long> questionIds = questionIdList.stream().map(id -> Long.valueOf(id.toString())).collect(Collectors.toList());
        LambdaQueryWrapper<Question> queryWrapper = Wrappers.lambdaQuery(Question.class)
                        .in(Question::getId,questionIds);
        // select * from question where id in()
        List<Question> questionList = questionMapper.selectList(queryWrapper);
        return questionList;
    }


    String luaScript = "local questionKey = KEYS[1]; local userId = ARGV[1]; " +
            "if redis.call('SISMEMBER', questionKey, userId) == 1 then " +
            "redis.call('SREM', questionKey, userId); return 0; " +
            "else redis.call('SADD', questionKey, userId); return 1; end";

    String script = "local questionKey = KEYS[1]; local userSetKey = KEYS[2]; " +
            "local userId = ARGV[1]; local questionId = ARGV[2]; " +
            "if redis.call('SISMEMBER', questionKey, userId) == 1 then " +
            "redis.call('SREM', questionKey, userId); " +
            "redis.call('SREM', userSetKey, questionId); return 0; " +
            "else redis.call('SADD', questionKey, userId); " +
            "redis.call('SADD', userSetKey, questionId); return 1; end";

}




