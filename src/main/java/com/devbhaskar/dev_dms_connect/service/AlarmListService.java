package com.devbhaskar.gsd_kafka.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devbhaskar.gsd_kafka.model.AlarmList;
import com.devbhaskar.gsd_kafka.repository.AlarmListRepository;

@Service
public class AlarmListService {

    @Autowired
    private AlarmListRepository repo;

    public AlarmList saveAlarm(AlarmList alarm) {
        return repo.save(alarm);
    }

    public Optional<AlarmList> getByUniqueId(String id) {
        return repo.findByAlarmUniqueId(id);
    }

    public List<AlarmList> getAll() {
        return repo.findAll();
    }
    
    public int deleteBurstAlarms(String backendId) {
        return repo.deleteByKeyword(backendId);
    }
}

