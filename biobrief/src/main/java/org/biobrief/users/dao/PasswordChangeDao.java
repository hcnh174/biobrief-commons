package org.biobrief.users.dao;

import org.biobrief.mongo.AbstractMongoDao;
import org.biobrief.users.entities.PasswordChange;
import org.biobrief.users.repositories.PasswordChangeRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordChangeDao extends AbstractMongoDao<PasswordChange, PasswordChangeRepository>
{

}
