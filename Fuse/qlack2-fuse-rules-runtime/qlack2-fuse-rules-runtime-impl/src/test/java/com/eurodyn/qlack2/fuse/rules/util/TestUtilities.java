package com.eurodyn.qlack2.fuse.rules.util;

public class TestUtilities {

    public static String testRule(){

        return "package rules.test;\n"
            + "\n"
            + "import com.eurodyn.qlack2.fuse.rules.util.FactDTO;\n"
            + "import java.util.List;\n"
            + "\n"
            + "global List<String> warnings;\n"
            + "\n"
            + "rule \"Test Rule\"\n"
            + "\n"
            + "  when\n"
            + "     FactDTO( age > 18)\n"
            + "  then\n"
            + "    warnings.add(\"Test Rule fired\");\n"
            + "end";

    }

}