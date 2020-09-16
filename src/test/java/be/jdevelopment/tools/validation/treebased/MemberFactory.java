package be.jdevelopment.tools.validation.treebased;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.property.MonadOfProperties;
import be.jdevelopment.tools.validation.property.Property;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MemberFactory {

    private final MonadOfProperties monad;

    MemberFactory(MonadOfProperties monad) {
        this.monad = monad;
    }

    private static class MutMember {
        String name;
        List<Member> underlings = List.of();

        public void setName(String name) {
            this.name = name;
        }

        public void setUnderlings(Iterator<Member> underlings) {
            this.underlings = StreamSupport.stream(Spliterators.spliteratorUnknownSize(underlings, Spliterator.ORDERED), false).collect(Collectors.toList());
        }
    }

    Member create(ObjectProvider provider) {
        MutMember mutMember = new MutMember();

        new ValidationProcess(provider, monad)
                .addStep(MemberProperties.NAME, MemberFactory::NameValidation, mutMember::setName)
                .addCollectionSteps(MemberProperties.UNDERLINGS,
                        MemberFactory::validateMembersCollection,
                        MemberFactory::validateMember,
                        mutMember::setUnderlings);

        return new Member(mutMember.name, mutMember.underlings);
    }

    static Pattern patName = Pattern.compile("[\\w ]+");
    private static Property<String> NameValidation(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("nullValue")
                .map(String.class::cast)
                .filter(arg -> patName.matcher(arg).matches())
                .registerFailureCode("invalidCharacters");
    }

    private static Property<Iterator<Object>> validateMembersCollection
            (Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("nullValue")
                .map(Object[].class::cast)
                .map(arr -> Arrays.stream(arr).iterator());
    }

    private static Property<Member> validateMember(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("nullValue")
                .map(ObjectProvider.class::cast)
                .map(arg -> new MemberFactory(monad).create(arg));
    }

}
