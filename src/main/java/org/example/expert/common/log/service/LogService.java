package org.example.expert.common.log.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.common.log.dto.request.LogRequest;
import org.example.expert.common.log.dto.response.LogResponse;
import org.example.expert.common.log.entity.Log;
import org.example.expert.common.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LogResponse saveLog(LogRequest logRequest) {
        Log log = new Log(logRequest.action(), logRequest.details());
        Log savedLog = logRepository.save(log);
        return new LogResponse(savedLog.getId(), savedLog.getAction(), savedLog.getDetails(), savedLog.getCreatedAt());
    }
}
