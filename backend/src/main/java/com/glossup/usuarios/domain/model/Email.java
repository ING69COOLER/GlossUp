package com.glossup.usuarios.domain.model;

import com.glossup.shared.domain.ReglaNegocioException;

import java.util.regex.Pattern;

/**
 * Objeto de valor que representa un correo electrónico válido y normalizado.
 *
 * <p>Regla de negocio: el formato debe ser válido; el valor se normaliza a minúsculas y sin
 * espacios para garantizar unicidad consistente (ver columna única {@code usuario.email}).</p>
 */
public record Email(String valor) {

    private static final Pattern PATRON =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El correo no puede estar vacío");
        }
        valor = valor.trim().toLowerCase();
        if (!PATRON.matcher(valor).matches()) {
            throw new ReglaNegocioException("El correo no tiene un formato válido: " + valor);
        }
    }

    @Override
    public String toString() {
        return valor;
    }
}
