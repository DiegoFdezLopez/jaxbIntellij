# Exposición GrupoMontana

---

<details>
<summary><strong>1. Desacoplamiento y SRP</strong></summary>
<br>

**Clase:** `Main`

- Creamos el DAO con la ruta XML mediante una constante (no hardcode)
- Declaramos la variable `gestion` **fuera del try** para que su ámbito abarque todo el método
- Le inyectamos el DAO a la Librería manualmente desde fuera

> *"La Librería no sabe nada de cómo cargar, guardar u obtener datos del XML."*

> *"Cambiar la fuente de datos solo implicaría modificar la clase DAO, sin tocar la lógica de negocio."*

> *"Esto es el Principio de Responsabilidad Única (SRP): cada clase tiene una sola razón para cambiar."*

</details>

---

<details>
<summary><strong>2. Doble Sistema de Excepciones y Programación Defensiva</strong></summary>
<br>

**Clase:** `GrupoMontanaLibreria` — Método `altaSenderista`

En la declaración del método usamos `throws JAXBException` (**checked exception** — el compilador nos obliga a gestionarla) para cualquier error relacionado con cargar, guardar o leer desde la fuente de datos.

Dentro del método usamos `IllegalArgumentException` (**unchecked exception**) para gestionar los errores relacionados con los datos en sí.

Usamos `throw new` para instanciar y lanzar la excepción con un **mensaje descriptivo** que facilita el diagnóstico del error.

### Dos niveles de defensa:

- **Nivel 1 — Datos no nulos o vacíos** → validación en el `if` antes de operar
- **Nivel 2 — Integridad del negocio** → comprobación de duplicados por email

</details>

---

<details>
<summary><strong>3. El DAO gestiona el Ciclo Completo de Persistencia</strong></summary>
<br>

**Clase:** `GrupoMontanaDAO` — ilustrado en el método `crearActividad` de `GrupoMontanaLibreria`

```java
// Modificación en memoria (RAM)
dao.getDatos().getRegistroActividades().getActividad().add(nuevaActividad);

// Persistencia en disco (XML)
dao.guardarDatos();
```

- Al **cargar datos**: JAXB **deserializa** el XML a objetos Java en RAM (`Unmarshaller`)
- Al **guardar datos**: JAXB **serializa** los objetos Java de vuelta al fichero XML (`Marshaller`)

> *"La lógica nunca toca el XML directamente. Solo trabaja con objetos Java."*

</details>