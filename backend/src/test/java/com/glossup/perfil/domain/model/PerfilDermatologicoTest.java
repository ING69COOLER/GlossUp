package com.glossup.perfil.domain.model;

import com.glossup.shared.domain.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PerfilDermatologicoTest {

    @Test
    void gestiona_alergias_dinamicamente_sin_duplicados() {
        PerfilDermatologico perfil = PerfilDermatologico.crear(1L, new Ph(5.2), TipoPiel.MIXTA, "liso");
        perfil.agregarAlergia("Parfum");
        perfil.agregarAlergia("parfum"); // mismo (normalizado) -> no duplica
        assertEquals(1, perfil.getAlergias().size());
        assertTrue(perfil.esAlergicoA("PARFUM"));

        perfil.quitarAlergia("parfum");
        assertFalse(perfil.esAlergicoA("parfum"));
    }

    @Test
    void ph_fuera_de_rango_no_es_valido() {
        assertThrows(ReglaNegocioException.class, () -> new Ph(15));
    }
}
