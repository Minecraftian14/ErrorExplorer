package be.jdevelopment.tools.validation.treebased;

import java.util.List;

public record Member(String name, List<Member> underlings) {

    @Override
    public String toString() {
        return "{\n" +
                "\"name\": \"" + name + "\",\n" +
                "\"underlings\": " + underlings +
                "\n}\n";
    }

    public int getDepth() {
        int i = 1;
        if (underlings.size() == 0)
            return i;
        else {
            for (Member member : underlings) {
                i = Math.max(i, member.getDepth(1));
            }
        }
        return i;
    }

    public int getDepth(int i) {
        int j = i + 1;
        if (underlings.size() == 0)
            return j;
        else {
            for (Member member : underlings) {
                j = Math.max(j, member.getDepth(i + 1));
            }
        }
        return j;
    }

}
