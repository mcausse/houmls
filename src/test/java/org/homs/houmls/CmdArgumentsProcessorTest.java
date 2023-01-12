package org.homs.houmls;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class CmdArgumentsProcessorTest {

    @Test
    void name() {
        String[] args = {"--ip=127.0.0.1", "v8.txt", "--port=23"};

        var sut = new CmdArgumentsProcessor(args);

        // Act
        sut.processArgs();

        assertThat(sut.files.size()).isEqualTo(1);
        assertThat(sut.files.toString()).isEqualTo("[v8.txt]");

        assertThat(sut.modifiers.size()).isEqualTo(2);
        assertThat(sut.modifiers.toString()).isEqualTo("{ip=127.0.0.1, port=23}");
    }

    @Test
    void error() {

        String[] args = {"--ip=127.0.0.1", "v8.txt", "--port22222223"};

        var sut = new CmdArgumentsProcessor(args);

        try {
            // Act
            sut.processArgs();

            fail("an exception should be thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("unexpected argument: --port22222223");
        }
    }
}
