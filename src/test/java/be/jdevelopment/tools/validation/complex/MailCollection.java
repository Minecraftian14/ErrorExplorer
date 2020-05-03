package be.jdevelopment.tools.validation.complex;

import java.util.Iterator;

interface MailCollection {

    Iterator<String> getMails();

    String getPreferredMail();

}
