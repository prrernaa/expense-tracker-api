package com.prerna.expense_tracker.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitorService {

    @Autowired
    private VisitorRepository repository;

    public Visitor saveVisitorInfo(Visitor visitor) {
        return repository.save(visitor);
    }

}