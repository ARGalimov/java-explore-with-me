package ru.practicum.ewm.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.practicum.ewm.constant.Constant.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkUserName(userDto);
        User user = userRepository.save(UserMapper.toNewEntity(userDto));
        log.info("Created user {} and reverting to controller as dto", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers(List<Integer> ids, PageRequest page) {
        List<User> users;
        if (Objects.isNull(ids)) {
            users = userRepository.findAllOrderById(page);
        } else {
            users = userRepository.findAllByIdIn(ids);
        }
        log.info("Found users {} and reverting to controller as dto", users);
        return UserMapper.toUsersDto(users);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User user = findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MSG, NOT_FOUND_ID_REASON));
        userRepository.deleteUserById(userId);
        log.info("Deleted user {} ", user);
    }

    @Override
    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }

    private void checkUserName(UserDto userDto) {
        Optional<User> category = userRepository.findByName(userDto.getName());
        if (category.isPresent()) {
            throw new ConflictException(INCORRECT_DATA_INPUT_MSG, INCORRECT_NAME_UNIQUE_REASON);
        }
    }

}