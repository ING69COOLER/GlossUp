package com.glossup.usuarios.domain.port.out;

/**
 * Puerto de salida para el cifrado y verificación de contraseñas. La capa de aplicación solo
 * conoce este contrato; el algoritmo concreto (BCrypt) vive en el adaptador de infraestructura.
 * Así la contraseña en claro nunca se almacena ni sale del flujo de autenticación.
 */
public interface CifradorDePassword {

    /** Devuelve el hash seguro de una contraseña en claro. */
    String cifrar(String passwordPlano);

    /** ¿La contraseña en claro coincide con el hash almacenado? */
    boolean coincide(String passwordPlano, String hashAlmacenado);
}
