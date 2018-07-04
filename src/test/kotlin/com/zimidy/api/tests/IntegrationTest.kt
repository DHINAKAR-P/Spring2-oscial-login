package com.zimidy.api.tests

import org.junit.Before
import org.junit.runner.RunWith
import org.neo4j.ogm.session.Session
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
abstract class IntegrationTest {

    @Autowired
    lateinit var session: Session

    @Before
    fun purgeDatabase() = session.purgeDatabase()
}
