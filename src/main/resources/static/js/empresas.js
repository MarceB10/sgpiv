const buscador = document.getElementById("buscador");
const filtroEstado = document.getElementById("filtroEstado");
const empresas = document.querySelectorAll(".empresa-card");

function filtrarEmpresas() {
    const texto = buscador.value.toLowerCase().trim();
    const estadoSeleccionado = filtroEstado.value;

    empresas.forEach(empresa => {
        const razonSocial = empresa.dataset.razonSocial;
        const estado = empresa.dataset.estado;

        const coincideTexto = razonSocial.includes(texto);
        const coincideEstado = !estadoSeleccionado || estado === estadoSeleccionado;

        empresa.style.display = coincideTexto && coincideEstado ? "flex" : "none";
    });
}

buscador.addEventListener("input", filtrarEmpresas);
filtroEstado.addEventListener("change", filtrarEmpresas);