package org.homs.lechugauml;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GridControlTest {

    @Test
    void integer() {

        assertThat(GridControl.engrid(0)).isEqualTo(0);
        assertThat(GridControl.engrid(1)).isEqualTo(0);
        assertThat(GridControl.engrid(4)).isEqualTo(0);
        assertThat(GridControl.engrid(5)).isEqualTo(10);
        assertThat(GridControl.engrid(6)).isEqualTo(10);
        assertThat(GridControl.engrid(8)).isEqualTo(10);
        assertThat(GridControl.engrid(10)).isEqualTo(10);
        assertThat(GridControl.engrid(12)).isEqualTo(10);
        assertThat(GridControl.engrid(18)).isEqualTo(20);

        assertThat(GridControl.engrid(0)).isEqualTo(0);
        assertThat(GridControl.engrid(-1)).isEqualTo(0);
        assertThat(GridControl.engrid(-4)).isEqualTo(0);
        assertThat(GridControl.engrid(-5)).isEqualTo(0);
        assertThat(GridControl.engrid(-6)).isEqualTo(-10);
        assertThat(GridControl.engrid(-8)).isEqualTo(-10);
        assertThat(GridControl.engrid(-10)).isEqualTo(-10);
        assertThat(GridControl.engrid(-12)).isEqualTo(-10);
        assertThat(GridControl.engrid(-18)).isEqualTo(-20);
    }
}