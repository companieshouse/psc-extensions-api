//package uk.gov.companieshouse.psc.extensions.api.service.impl;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.stereotype.Service;
//import uk.gov.companieshouse.email_producer.EmailProducer;
//import uk.gov.companieshouse.email_producer.EmailSendingException;
//import uk.gov.companieshouse.email_producer.model.EmailData;
//import uk.gov.companieshouse.psc.extensions.api.PscExtensionsApiApplication;
//import uk.gov.companieshouse.psc.extensions.api.mongo.document.PscExtension;
//import uk.gov.companieshouse.psc.extensions.api.mongo.document.SecondExtensionEmailData;
//import uk.gov.companieshouse.psc.extensions.api.service.EmailService;
//
//@Service
//@ComponentScan("uk.gov.companieshouse.email_producer")
//public class EmailServiceImpl implements EmailService {
//
//    private static final Logger LOG = LoggerFactory.getLogger(PscExtensionsApiApplication.APPLICATION_NAMESPACE);
//    private final EmailProducer emailProducer;
//    @Autowired
//    public EmailServiceImpl(EmailProducer emailProducer) {
//        this.emailProducer = emailProducer;
//    }
//
//    @Override
//    public PscExtension sendSecondExtensionGrantedEmail(final String recipientEmailAddress, final String fullName) {
//        final var emailData = new SecondExtensionEmailData();
//        emailData.setTo(recipientEmailAddress);
//        emailData.setFullName(fullName);
//        sendEmail(emailData, "second_extension_granted_email_english");
//        return null;
//    }
//
//    private void sendEmail(final EmailData emailData, final String messageType) throws EmailSendingException {
//        try {
//            emailProducer.sendEmail(emailData, messageType);
//            LOG.debug(String.format("Submitted %s email to Kafka", messageType));
//        } catch (EmailSendingException exception) {
//            LOG.error("Error sending email", exception);
//            throw exception;
//        }
//    }
//}
