package com.glossup.usuarios.domain.port.out;

import java.util.List;

/**
 * Puerto de salida para la emisión de tokens de acceso (JWT, RNF-11). La capa de aplicación pide
 * un token para un usuario y sus roles; el adaptador decide el formato y la firma.
 *
 * <p>Contrato clave para el resto del sistema: el <em>subject</em> del token es el id del usuario,
 * de modo que los controladores (p. ej. perfil HU1/HU2) obtienen el usuario autenticado de él.</p>
 */
public interface GeneradorDeToken {

    String generar(Long usuarioId, List<String> roles);
}
