package com.rainbow.sof.domain.user.mapper;

import com.rainbow.sof.domain.answer.dto.AnswerDto;
import com.rainbow.sof.domain.question.dto.QuestionDto;
import com.rainbow.sof.domain.user.entity.User;
import com.rainbow.sof.domain.user.dto.UserToJoinDto.MyPageResponseDto;
import com.rainbow.sof.domain.user.dto.singleDto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User userSignupPostToUser(UserDto.SignUpPost signUpPost);
    UserDto.Response userToUserUserDtoResponse(User user);

    User userLoginPostTouser(UserDto.CreationLoginDto creationLoginDto);
    UserDto.LoginResponse userToLoginDto(User user);

    default MyPageResponseDto userToMyPageDto(User user){
        List<QuestionDto.MyPageQuestionResponse> questionList = user.getQuestionList().stream()
                .map(question -> QuestionDto.MyPageQuestionResponse.builder()
                        .questionId(question.getQuestionId())
                        .content(question.getContent())
                        .title(question.getTitle())
                        .createdAt(question.getCreatedAt())
                        .modifiedAt(question.getModifiedAt())
                        .build())
                .collect(Collectors.toList());

        List<AnswerDto.MyPageAnswerResponse> AnsweList = user.getAnswerList().stream()
                .map(answer ->AnswerDto.MyPageAnswerResponse.builder()
                        .answerId(answer.getAnswerId())
                        .createdAt(answer.getModifiedAt())
                        .modifiedAt(answer.getModifiedAt())
                        .content(answer.getContent())
                        .build())
                .collect(Collectors.toList());

        return MyPageResponseDto.builder()
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .questionList(questionList)
                .AnswerList(AnsweList)
                .build();
    }
}
