package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.property.Property;
import be.jdevelopment.tools.validation.property.MonadOfProperties;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.regex.Pattern.compile;

class PersonFactory {

    private final MonadOfProperties monad;
    PersonFactory(MonadOfProperties monad) {
        this.monad = monad;
    }

    private static class MutPerson {
        Address address;
        String[] emailAddresses;
        int defaultAddress;

        void setAddress(Address arg) { address = arg; }
        void setEmailAddresses(Iterator<String> arg) {
            emailAddresses = StreamSupport.stream(Spliterators.spliteratorUnknownSize(arg, Spliterator.ORDERED), false)
                    .toArray(String[]::new);
        }
        void setDefaultEmailIndex(int arg) { defaultAddress = arg; }
    }

    private static class MailCollectionImpl extends HashSet<String> implements MailCollection {
        private final String preferred;
        MailCollectionImpl(String[] availables, int preferred) {
            super(Optional.ofNullable(availables).stream()
                    .flatMap(Arrays::stream)
                    .collect(Collectors.toList()));
            this.preferred = availables == null || availables.length == 0 ? null : availables[preferred];
        }

        @Override
        public Iterator<String> getMails() {
            return iterator();
        }

        @Override
        public String getPreferredMail() {
            return preferred;
        }
    }

    Person create(ObjectProvider provider) {
        MutPerson mutPerson = new MutPerson();

        ValidationProcess process = new ValidationProcess(provider, monad)
            .addCollectionSteps(PersonProperty.EMAIL,
                PersonFactory::validateEmailAddressCollection,
                PersonFactory::validateEmailAddress,
                mutPerson::setEmailAddresses)
            .addStep(PersonProperty.ADDRESS, PersonFactory::validateAddress, mutPerson::setAddress);

        ValidationProcess.ValidationRule<Integer> validateDefault = (source, monad) -> monad.of(source)
                .filter(Objects::nonNull)
                .match((state, value) -> switch(state) { // default was provided, we validate
                    case SUCCESS -> monad.of(value).filter(String.class::isInstance)
                                .map(String.class::cast)
                                .flatMap($ -> monad.of(provider.provideFor(PersonProperty.EMAIL))
                                        .filter(Objects::nonNull)
                                        .filter(Object[].class::isInstance)
                                        .map(Object[].class::cast)
                                        .map(Arrays::asList)
                                        .map(lst -> lst.indexOf($))
                                        .filter(x -> x > -1)
                                )
                                .registerFailureCode("notfound");
                    case FAILURE -> null;
                });

        process.addStep(PersonProperty.DEFAULT_EMAIL, validateDefault, mutPerson::setDefaultEmailIndex);

        return new Person(new MailCollectionImpl(mutPerson.emailAddresses, mutPerson.defaultAddress), mutPerson.address);
    }

    private static Property<Iterator<Object>> validateEmailAddressCollection
            (Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(Object[].class::isInstance)
                .registerFailureCode("type")
                .map(Object[].class::cast)
                .map(arr -> {
                    monad.of(arr)
                            .filter(lst -> Arrays.stream(arr)
                                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                                    .values().stream()
                                    .noneMatch(count -> 1L < count))
                            .registerFailureCode("duplicates");
                    return Arrays.stream(arr).iterator();
                });
    }

    private final static Pattern EMAIL_PATTERN = compile("^[a-zA-Z0-9.\\-_]+@[a-zA-Z0-9.\\-_]+$");
    private static Property<String> validateEmailAddress(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(String.class::isInstance)
                .registerFailureCode(("type"))
                .map(String.class::cast)
                .filter(str -> EMAIL_PATTERN.matcher(str).matches())
                .registerFailureCode("format");
    }

    private static Property<Address> validateAddress
            (Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(ObjectProvider.class::isInstance)
                .registerFailureCode("type")
                .map(ObjectProvider.class::cast)
                .map(provider -> new AddressFactory(monad).create(provider));
    }

}
