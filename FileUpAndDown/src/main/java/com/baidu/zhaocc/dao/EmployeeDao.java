package com.baidu.zhaocc.dao;

import com.baidu.zhaocc.model.Employee;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @Auther: zhaochaochao
 * @Date: 2018/7/3
 * @Description:
 */
public class EmployeeDao {

    private static SessionFactory factory;
    public static int save(Employee employee) {
        try{
            factory = new Configuration().configure("Employee.cfg.xml").buildSessionFactory();
        }catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
        Session session = factory.openSession();
        Transaction tx = null;
        Integer employeeID = null;
        try{
            tx = session.beginTransaction();
            employeeID = (Integer) session.save(employee);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return employeeID;
    }
}
