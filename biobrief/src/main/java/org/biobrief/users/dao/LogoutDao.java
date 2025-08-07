package org.biobrief.users.dao;

import org.biobrief.mongo.AbstractMongoDao;
import org.biobrief.users.entities.Logout;
import org.biobrief.users.repositories.LogoutRepository;
import org.springframework.stereotype.Repository;

@Repository
public class LogoutDao extends AbstractMongoDao<Logout, LogoutRepository>
{

}
