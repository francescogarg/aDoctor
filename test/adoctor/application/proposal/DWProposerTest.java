package adoctor.application.proposal;

import adoctor.application.bean.proposal.MethodProposal;
import adoctor.application.bean.smell.DWSmell;
import adoctor.application.bean.smell.ERBSmell;
import adoctor.application.bean.smell.MethodSmell;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class DWProposerTest {

    @ParameterizedTest
    @MethodSource("computeProposalProvider")
    void computeProposal(MethodSmell methodSmell, MethodProposal oracle) throws IOException {
        DWProposer testedProposer = new DWProposer();
        MethodProposal result = testedProposer.computeProposal(methodSmell);
        assertEquals(result, oracle);
    }

    private static Stream<Arguments> computeProposalProvider() {
        ERBSmell erbSmell = new ERBSmell();
        DWSmell dwSmellInvalid = new DWSmell();

        /*
        dwSmellValid.setSourceFile(new File("testResources/testDW1.java"));
        MethodBean methodBean = new MethodBean();
        methodBean.setTextContent("" +
                "public void testMethod(){\n" +
                "  PowerManager.WakeLock wakeLock4=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,\"wakelock4\");\n" +
                "  wakeLock4.acquire();\n" +
                "}\n"
        );
        dwSmellValid.setMethodBean(methodBean);
        */

        return Stream.of(
                arguments(null, null),
                arguments(erbSmell, null),
                arguments(dwSmellInvalid, null)
        );
    }
}