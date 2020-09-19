package be.jdevelopment.tools.validation.ui;

import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.property.impl.Monads;
import be.jdevelopment.tools.validation.treebased.Member;
import be.jdevelopment.tools.validation.treebased.MemberFactory;
import be.jdevelopment.tools.validation.util.ObjectProviderHelper;

import java.io.IOException;
import java.util.HashSet;

public class ErrorExplorerTest  {

    public ErrorExplorerTest() throws IOException {

        HashSet<Failure> failures = new HashSet<>();
        FailureBuilder failureBuilder = FailureBuilder.getDefault(failures);

        var provider = ObjectProviderHelper.objectProviderFromJsonFile("treebased/membersWrongName.json");
        Member info = new MemberFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        new ErrorExplorer(failures).show();
    }

    public static void main(String[] args) throws IOException {
        new ErrorExplorerTest();
    }

}