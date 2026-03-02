# ApkServiciosBackend
📱 Sistema de Gestión de Cuentas de Streaming Compartidas Es una app móvil (Ionic + Angular) con backend (Spring Boot + WebFlux) que permite administrar y comercializar accesos a servicios de streaming como Netflix, Spotify, Disney+, etc.


El negocio que resuelve
Hay personas que compran una cuenta de Netflix con capacidad para 4 perfiles, y en lugar de usar todos ellos solos, venden o alquilan cada perfil individualmente a diferentes clientes. Esta app gestiona todo ese proceso.

Los tres pilares
Servicios — son las plataformas (Netflix, Spotify, etc.). Define cuántos perfiles base tiene cada cuenta y cuántos extras se pueden contratar, con sus precios.
Cuentas — es la cuenta maestra comprada. Tiene el correo, la clave, las fechas de vigencia y pertenece a un servicio. Al crearla, se generan automáticamente sus perfiles vacíos listos para asignar.
Perfiles — son los cupos dentro de esa cuenta. Cada perfil puede estar en tres estados: LIBRE (disponible), ACTIVO (ocupado por un cliente) o VENCIDO (el período del cliente expiró). A cada perfil se le asigna un cliente registrado con sus fechas de suscripción.

1. Crear servicio (Netflix, 4 perfiles, precio)
        ↓
2. Crear cuenta (correo + clave + fechas)
   → Se generan 4 perfiles LIBRES automáticamente
        ↓
3. Registrar clientes (nombre, apellido, teléfono)
        ↓
4. Asignar cliente a perfil LIBRE (seleccionar cliente + fechas de suscripción)
   → Perfil pasa a ACTIVO
        ↓
5. Cuando vence → liberar perfil → vuelve a LIBRE
   → Listo para un nuevo cliente
