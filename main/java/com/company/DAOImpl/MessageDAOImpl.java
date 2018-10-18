package com.company.DAOImpl;

import com.company.DAO.MessageDAO;
import com.company.Entity.Message;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("messageDAO")
public class MessageDAOImpl implements MessageDAO {

    @Autowired
    private SessionFactory sessionFactory;


}
