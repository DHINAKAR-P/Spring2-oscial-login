package com.zimidy.api.layers.domain

import freemarker.template.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
// import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils

// todo: consider usage of the following tools: https://github.com/ozimov/spring-boot-email-tools
// todo: mailing features are temporarily switched off, because authentication to mail account doesn't work
// @Service
class MailService(
    private val javaMailSender: JavaMailSender,
    private val freeMarker: Configuration
) {

    fun send(to: String, subject: String, text: String) {
        val mimeMessage = javaMailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage)
        mimeMessageHelper.setTo(to)
        mimeMessageHelper.setSubject(subject)
        mimeMessageHelper.setText(text)
        javaMailSender.send(mimeMessage)
    }

    fun send(to: String, subject: String, template: String, model: Map<String, *>) {
        val mailTemplate = freeMarker.getTemplate(template)
        val text = FreeMarkerTemplateUtils.processTemplateIntoString(mailTemplate, model)
        send(to, subject, text)
    }

    fun send(to: String, subject: String, template: String, vararg data: Pair<String, *>) {
        send(to, subject, template, mapOf(*data))
    }
}
