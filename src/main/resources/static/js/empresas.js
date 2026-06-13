const buscador = document.getElementById("buscador");
const filtroEstado = document.getElementById("filtroEstado");
const filas = document.querySelectorAll("#tablaEmpresas tr");

function filtrarEmpresas() {
    const texto = buscador.value.toLowerCase().trim();
    const estadoSeleccionado = filtroEstado.value;

    filas.forEach(fila => {
        const razonSocial = fila.dataset.razonSocial;
        const estado = fila.dataset.estado;

        const coincideNombre = razonSocial.startsWith(texto);
        const coincideEstado = !estadoSeleccionado || estado === estadoSeleccionado;

        fila.style.display = coincideNombre && coincideEstado ? "" : "none";
    });
}

buscador.addEventListener("input", filtrarEmpresas);
filtroEstado.addEventListener("change", filtrarEmpresas);