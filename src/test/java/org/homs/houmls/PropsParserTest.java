package org.homs.houmls;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropsParserTest {

    @Test
    void name0() {
        String text = "";

        var props = PropsParser.parseProperties(text);

        assertThat(props.toString()).isEqualTo("{=\n}");
    }

    @Test
    void name1() {
        String text = "jou\n";

        var props = PropsParser.parseProperties(text);

        assertThat(props.toString()).isEqualTo("{=jou\n}");
    }

    @Test
    void name2() {
        String text = "lt=juas";

        var props = PropsParser.parseProperties(text);

        assertThat(props.toString()).isEqualTo("{lt=juas, =}");
    }

    @Test
    void name3() {
        String text = "jou\nlt=123\njuas";

        var props = PropsParser.parseProperties(text);

        assertThat(props.toString()).isEqualTo("{lt=123, =jou\njuas\n}");
    }

    @Test
    void reverse() {
        assertThat(PropsParser.reverseArrowStyle("12345")).isEqualTo("54321");
        assertThat(PropsParser.reverseArrowStyle(">>>>>")).isEqualTo("<<<<<");
    }

//    @Test
//    void name() {
//        System.out.println(170/10);
//        System.out.println(102/6);
//        System.out.println(102*10/6);
//    }
}