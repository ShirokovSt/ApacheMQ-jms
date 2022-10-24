package com.example.project4.service

import com.example.project4.dto.*
import com.example.project4.repository.PersonRepository
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import java.util.*
import javax.jms.MessageConsumer

@Service
class ListenerService (
    private val personRepository: PersonRepository
){
    @JmsListener(destination = "\${spring.activemq.queues}")
    fun messageListener(message: String?) {
        val xmlMapper = XmlMapper()
        val listPersons  = xmlMapper.readValue(message, Array<PersonDto>::class.java).toList()
        for (person in listPersons)
            if(!personRepository.addPerson(person.name, person.lastName))
                logger.info("ignoring Person{ ${person.name}, ${person.lastName}}")
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MessageConsumer::class.java)
    }
}