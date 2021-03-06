package org.biobrief.users.dao;

import org.biobrief.mongo.AbstractMongoDao;
import org.biobrief.users.entities.Login;
import org.biobrief.users.repositories.LoginRepository;
import org.springframework.stereotype.Repository;

@Repository
public class LoginDao extends AbstractMongoDao<Login, LoginRepository>
{

}