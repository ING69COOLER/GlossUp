package com.glossup.usuarios.domain.model;

/**
 * Roles de negocio del sistema (actores descritos en la Definición del Proyecto).
 * <ul>
 *   <li>{@code CLIENTE}: usuario final que compra y gestiona su perfil dermatológico.</li>
 *   <li>{@code VENDEDOR}: marca/proveedor que gestiona su catálogo de productos.</li>
 *   <li>{@code ADMIN}: administrador de la plataforma.</li>
 * </ul>
 */
public enum Rol {
    CLIENTE,
    VENDEDOR,
    ADMIN
}
