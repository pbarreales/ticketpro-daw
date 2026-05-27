document.addEventListener("DOMContentLoaded", () => {

    // 1. EXTRAER DATOS DE LA SESIÓN
    const nombre = sessionStorage.getItem("nombreUsuario");
    const rol = sessionStorage.getItem("rolUsuario");
    const idUsuario = sessionStorage.getItem("usuarioId");

    // Si no hay sesión, patada al login
    if (!nombre || !rol) {
        window.location.href = "/login";
        return;
    }

    // 2. INYECTAR DATOS EN LA CABECERA
    document.getElementById("nombreUsuario").innerText = nombre;
    document.getElementById("rolUsuario").innerText = rol;

    const seccionCrear = document.getElementById("seccion-crear-ticket");
    const panelAdmin = document.getElementById("panelAdmin");
    const tituloTabla = document.getElementById("tituloTablaTickets");
    const columnaAcciones = document.getElementById("columnaAcciones");


    // 3. 🌟 RENDERIZADO CONDICIONAL BLINDADO (Solo 'USUARIO', invulnerable a mayúsculas/minúsculas)
    if (rol.toUpperCase() === "USUARIO") {
        seccionCrear.style.display = "block";
        tituloTabla.innerText = "Mis Tickets";
        cargarTicketsCliente(idUsuario);

    } else if (rol.toUpperCase() === "INFORMATICO") {
        seccionCrear.style.display = "none";
        tituloTabla.innerText = "Gestión de Tickets";
        if (columnaAcciones) columnaAcciones.style.display = "table-cell";
        cargarTicketsGestion(rol);

    } else if (rol.toUpperCase() === "ADMIN") {
        seccionCrear.style.display = "none";
        if (panelAdmin) panelAdmin.style.display = "block";
        tituloTabla.innerText = "Monitorización Global de Tickets";
        if (columnaAcciones) columnaAcciones.style.display = "table-cell";
        cargarTicketsGestion(rol);
        cargarUsuariosAdmin();
    }

    // 4. CERRAR SESIÓN
    document.getElementById("btnSalir").addEventListener("click", () => {
        sessionStorage.clear();
        window.location.href = "/login";
    });

    // 5. CREAR TICKET (POST) - Solo lo ven los clientes
    document.getElementById("formTicket").addEventListener("submit", async (e) => {
        e.preventDefault();

        const nuevoTicket = {
            titulo: document.getElementById("tituloTicket").value,
            descripcion: document.getElementById("descTicket").value,
            prioridad: document.getElementById("prioridadTicket").value,
            clienteId: idUsuario
        };

        try {
            const respuesta = await fetch('/api/incidencias', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(nuevoTicket)
            });

            if (respuesta.ok) {
                alert("¡Ticket creado con éxito!");
                document.getElementById("formTicket").reset();
                cargarTicketsCliente(idUsuario);
            } else {
                alert("Error al crear el ticket");
            }
        } catch (error) {
            console.error("Error en la conexión:", error);
        }
    });
});

// ==========================================
// 🚀 CONEXIONES CON EL BACKEND (FETCH API)
// ==========================================

async function cargarTicketsCliente(clienteId) {
    const respuesta = await fetch(`/api/incidencias/cliente?clienteId=${clienteId}`);
    const incidencias = await respuesta.json();
    pintarTablaTickets(incidencias, false);
}

async function cargarTicketsGestion(rol) {
    const respuesta = await fetch(`/api/incidencias`);
    const incidencias = await respuesta.json();
    pintarTablaTickets(incidencias, true);
}

function pintarTablaTickets(incidencias, mostrarAcciones) {
    const tbody = document.getElementById("tablaTicketsBody");
    tbody.innerHTML = "";

    let abiertos = 0;
    let progreso = 0;
    let resueltos = 0;

    if (incidencias.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align: center;">No hay tickets disponibles.</td></tr>`;
        actualizarTarjetasContadores(0, 0, 0, 0);
        return;
    }

    incidencias.forEach(ticket => {
        if (ticket.estado.toUpperCase() === 'ABIERTO') abiertos++;
        else if (ticket.estado.toUpperCase() === 'EN PROGRESO') progreso++;
        else if (ticket.estado.toUpperCase() === 'RESUELTO') resueltos++;

        let clasePrioridad = ticket.prioridad === 'Alta' || ticket.prioridad === 'Crítica' ? 'p-alta' : ticket.prioridad === 'Media' ? 'p-media' : 'p-baja';
        let claseEstado = ticket.estado.toUpperCase() === 'RESUELTO' ? 'e-resuelto' : ticket.estado.toUpperCase() === 'ABIERTO' ? 'e-abierto' : 'e-progreso';

        let fila = document.createElement("tr");
        fila.innerHTML = `
            <td>#${ticket.id}</td>
            <td><strong>${ticket.titulo}</strong></td>
            <td><span class="prioridad ${clasePrioridad}">${ticket.prioridad || 'Media'}</span></td>
            <td><span class="estado ${claseEstado}">${ticket.estado}</span></td>
            <td>${ticket.fechaCreacion ? new Date(ticket.fechaCreacion).toLocaleDateString() : 'Hoy'}</td>
        `;

        if (mostrarAcciones) {
            fila.innerHTML += `
                <td>
                    <select onchange="cambiarEstadoTicket(${ticket.id}, this.value)" style="padding: 0.25rem; border-radius: 4px;">
                        <option value="">Cambiar...</option>
                        <option value="Abierto">Abierto</option>
                        <option value="En Progreso">En Progreso</option>
                        <option value="Resuelto">Resuelto</option>
                    </select>
                </td>
            `;
        }
        tbody.appendChild(fila);
    });

    actualizarTarjetasContadores(abiertos, progreso, resueltos, incidencias.length);
}

function actualizarTarjetasContadores(abiertos, progreso, resueltos, total) {
    if (document.getElementById("statAbiertos")) document.getElementById("statAbiertos").innerText = abiertos;
    if (document.getElementById("statProgreso")) document.getElementById("statProgreso").innerText = progreso;
    if (document.getElementById("statResueltos")) document.getElementById("statResueltos").innerText = resueltos;
    if (document.getElementById("statTotal")) document.getElementById("statTotal").innerText = total;
}

// ACTUALIZAR ESTADO DEL TICKET (PUT)
async function cambiarEstadoTicket(idTicket, nuevoEstado) {
    if (!nuevoEstado) return;

    await fetch(`/api/incidencias/${idTicket}/estado?nuevoEstado=${nuevoEstado}`, {
        method: 'PUT'
    });

    cargarTicketsGestion(sessionStorage.getItem("rolUsuario"));
}

// CARGAR USUARIOS PARA EL ADMIN
async function cargarUsuariosAdmin() {
    const respuesta = await fetch('/api/usuarios');
    if (respuesta.ok) {
        const usuarios = await respuesta.json();
        const contenedor = document.getElementById("contenedorUsuariosAdmin");

        let html = `
            <table style="width:100%; border-collapse: collapse; margin-top: 1rem; background: white; border-radius: 6px; overflow: hidden;">
                <thead>
                    <tr style="background: #f3f4f6; text-align: left;">
                        <th style="padding: 10px;">ID</th>
                        <th style="padding: 10px;">Nombre</th>
                        <th style="padding: 10px;">Email</th>
                        <th style="padding: 10px;">Rol</th>
                        <th style="padding: 10px;">Acción</th>
                    </tr>
                </thead>
                <tbody>
        `;

        usuarios.forEach(u => {
            const botonTexto = u.rol === 'INFORMATICO' ? '✖ Quitar Técnico' : '🛠 Hacer Técnico';
            const nuevoRolDestino = u.rol === 'INFORMATICO' ? 'cliente' : 'INFORMATICO';

            html += `
                <tr style="border-bottom: 1px solid #e5e7eb;">
                    <td style="padding: 10px;">#${u.id}</td>
                    <td style="padding: 10px;"><strong>${u.nombre}</strong></td>
                    <td style="padding: 10px;">${u.email}</td>
                    <td style="padding: 10px;"><span class="badge-rol">${u.rol}</span></td>
                    <td style="padding: 10px;">
                        <button onclick="cambiarRolUsuario(${u.id}, '${nuevoRolDestino}')" class="btn-primario" style="padding: 4px 8px; font-size: 0.8rem; background-color: ${u.rol === 'INFORMATICO' ? '#ef4444' : '#3b82f6'}">
                            ${botonTexto}
                        </button>
                    </td>
                </tr>
            `;
        });

        html += '</tbody></table>';
        contenedor.innerHTML = html;
    }
}

// CAMBIAR ROL DE USUARIO EN CALIENTE (PUT)
async function cambiarRolUsuario(idUsuario, nuevoRol) {
    const respuesta = await fetch(`/api/usuarios/${idUsuario}/rol?nuevoRol=${nuevoRol}`, {
        method: 'PUT'
    });

    if (respuesta.ok) {
        alert("¡Rol actualizado en MySQL!");
        cargarUsuariosAdmin();
    }
}

// Exponer funciones al ámbito global para los onclick de HTML
window.cambiarRolUsuario = cambiarRolUsuario;
window.cambiarEstadoTicket = cambiarEstadoTicket;