package com.example.project4

import com.example.project4.repository.PersonRepository
import com.example.project4.repository.PersonRepositoryImpl
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jms.core.JmsTemplate
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import javax.jms.QueueBrowser
import javax.jms.Session


@RunWith(SpringRunner::class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Project4ApplicationTests {

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate

    @Value("\${spring.activemq.queues}")
    private lateinit var queueName: String

    private var name = "Zahar555"
    private var lastName = "Dostoevskii555"

    @Test
    fun `1 - testPersonListener`() {
        val message = """<persons>
		   <person>
			  <name>$name</name>
			  <last-name>$lastName</last-name>
		   </person>
		</persons>"""
        jmsTemplate.convertAndSend(queueName, message)

        waitForEmptyQueue(queueName)
        waitForAddRepository(name, lastName)
        assertFalse(personRepository.addPerson(name, lastName))
        deleteTestPersonsInRepository(name, lastName)
    }

    @Test
    fun `2 - testAddToRepository`() {
        assertTrue(personRepository.addPerson(name, lastName))
    }

    @Test
    fun `3 - testAddDuplicateToRepository`() {
        assertFalse(personRepository.addPerson(name, lastName))
        deleteTestPersonsInRepository(name, lastName)
        deleteTestPersonsInRepository(name, lastName)
    }

    fun waitForEmptyQueue(queueName: String) {
        while (jmsTemplate.browseSelected(
                queueName, "true = true"
            ) { _: Session?, qb: QueueBrowser -> Collections.list(qb.enumeration).size }!! != 0
        )
            continue
    }

    fun waitForAddRepository(name: String, lastName: String) {
        while (jdbcTemplate.query(
                "select * from person where name = :name and last_name = :lastName",
                mapOf(
                    "name" to name,
                    "lastName" to lastName
                ),
                PersonRepositoryImpl.ROW_MAPPER
            ).firstOrNull() == null
        )
            continue
    }

    fun deleteTestPersonsInRepository(name: String, lastName: String) {
        jdbcTemplate.update(
            "delete from person where name = :name and last_name = :lastName",
            mapOf(
                "name" to name,
                "lastName" to lastName
            )
        )
    }

}
