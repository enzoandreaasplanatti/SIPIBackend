package org.example.sipibackend.service;

import org.example.sipibackend.repository.FilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilterServiceImpl implements FilterService {
    @Autowired
    private FilterRepository filterRepository;
}
