import { useEffect, useMemo, useState, type ReactNode } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../shared/auth/AuthContext'
import {
  actualizarStock,
  agregarIngrediente,
  crearProducto,
  despublicarProducto,
  editarProducto,
  listarMisProductos,
  mensajeDeError,
  publicarProducto,
  quitarIngrediente,
  type IngredienteFicha,
  type ProductoVendedor,
} from '../../features/catalogo/api/productos'

const COP = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 })

type Vista = 'dashboard' | 'productos' | 'editor' | 'pedidos' | 'ventas' | 'ajustes'

/** Estado comercial de un producto para la etiqueta de la tabla (según mockup). */
function estadoDe(p: ProductoVendedor): { txt: string; cls: string } {
  if (p.stock === 0) return { txt: 'Sin stock', cls: 'rojo' }
  if (p.activo) return { txt: 'Indexado', cls: 'ok' }
  if (p.ingredientes.length === 0) return { txt: 'Rev. INCI', cls: 'amber' }
  return { txt: 'Borrador', cls: 'amber' }
}

/**
 * Panel del VENDEDOR (mockups slides 15-17): layout con barra lateral y tres vistas —
 * Dashboard, Mis productos (tabla) y el editor de producto a dos columnas con la ficha INCI.
 * Pedidos/Ventas/Ajustes aún no tienen backend: quedan como marcador.
 */
export default function VendedorPanel() {
  const { sesion, salir } = useAuth()
  const navigate = useNavigate()
  const [productos, setProductos] = useState<ProductoVendedor[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [vista, setVista] = useState<Vista>('dashboard')
  const [editando, setEditando] = useState<ProductoVendedor | 'nuevo' | null>(null)

  useEffect(() => {
    listarMisProductos()
      .then(setProductos)
      .catch((e) => setError(mensajeDeError(e)))
      .finally(() => setCargando(false))
  }, [])

  function aplicar(p: ProductoVendedor) {
    setProductos((prev) => {
      const i = prev.findIndex((x) => x.id === p.id)
      if (i === -1) return [p, ...prev]
      const copia = [...prev]
      copia[i] = p
      return copia
    })
  }

  function abrirNuevo() {
    setEditando('nuevo')
    setVista('editor')
  }
  function abrirEditor(p: ProductoVendedor) {
    setEditando(p)
    setVista('editor')
  }

  function cerrarSesion() {
    salir()
    navigate('/login', { replace: true })
  }

  const nav: { id: Vista; label: string }[] = [
    { id: 'dashboard', label: 'Dashboard' },
    { id: 'productos', label: 'Mis productos' },
    { id: 'editor', label: 'Nuevo producto' },
    { id: 'pedidos', label: 'Pedidos' },
    { id: 'ventas', label: 'Ventas' },
    { id: 'ajustes', label: 'Ajustes' },
  ]

  return (
    <div className="vend-layout">
      <aside className="vend-side">
        <div className="vend-brand">GlossUP</div>
        <div className="vend-sub">Panel Vendedor</div>
        <nav className="vend-nav">
          {nav.map((n) => (
            <button
              key={n.id}
              type="button"
              className={vista === n.id ? 'activo' : ''}
              onClick={() => (n.id === 'editor' ? abrirNuevo() : setVista(n.id))}
            >
              <span className="dot" />
              {n.label}
            </button>
          ))}
        </nav>
        <div className="vend-side-pie">
          <span>{sesion?.nombre}</span>
          <button type="button" onClick={cerrarSesion}>
            Salir
          </button>
        </div>
      </aside>

      <main className="vend-main">
        {error && <p className="error">{error}</p>}
        {vista === 'dashboard' && (
          <Dashboard marca={sesion?.nombre ?? ''} productos={productos} onNuevo={abrirNuevo} />
        )}
        {vista === 'productos' && (
          <MisProductos productos={productos} cargando={cargando} onEditar={abrirEditor} onNuevo={abrirNuevo} />
        )}
        {vista === 'editor' && (
          <ProductoEditor
            inicial={editando === 'nuevo' ? null : (editando as ProductoVendedor)}
            onGuardado={(p) => {
              aplicar(p)
              setVista('productos')
            }}
            onCambio={aplicar}
            onVolver={() => setVista('productos')}
          />
        )}
        {(vista === 'pedidos' || vista === 'ventas' || vista === 'ajustes') && (
          <Placeholder titulo={nav.find((n) => n.id === vista)!.label} />
        )}
      </main>
    </div>
  )
}

/* ------------------------------------------------------------------ Dashboard */
function Dashboard({
  marca,
  productos,
  onNuevo,
}: {
  marca: string
  productos: ProductoVendedor[]
  onNuevo: () => void
}) {
  const activos = productos.filter((p) => p.activo).length
  const alertas = productos.filter((p) => p.ingredientes.length === 0).length

  return (
    <>
      <div className="vend-topline">
        <h2>Buenos días, {marca}</h2>
        <button type="button" className="btn-primario" onClick={onNuevo}>
          ＋ Nuevo producto
        </button>
      </div>

      <div className="tiles">
        <StatTile label="Ventas del mes" valor={COP.format(0)} cls="val-vino" />
        <StatTile label="Pedidos" valor="0" cls="val-rosa" />
        <StatTile label="Productos activos" valor={String(activos)} cls="val-ok" />
        <StatTile label="Alertas de INCI" valor={String(alertas)} cls="val-amber" />
      </div>

      <div className="dash-row">
        <section className="panel">
          <h3>Ventas por semana</h3>
          <p className="nota">Aún no hay ventas registradas.</p>
        </section>
        <section className="panel">
          <h3>Pedidos recientes</h3>
          <p className="nota">No hay pedidos aún.</p>
        </section>
      </div>
    </>
  )
}

function StatTile({ label, valor, cls }: { label: string; valor: string; cls: string }) {
  return (
    <div className="tile-stat">
      <span className="label">{label}</span>
      <span className={`val ${cls}`}>{valor}</span>
    </div>
  )
}

/* ------------------------------------------------------------- Mis productos */
function MisProductos({
  productos,
  cargando,
  onEditar,
  onNuevo,
}: {
  productos: ProductoVendedor[]
  cargando: boolean
  onEditar: (p: ProductoVendedor) => void
  onNuevo: () => void
}) {
  const [q, setQ] = useState('')
  const [filtro, setFiltro] = useState<'todos' | 'activos' | 'sinstock'>('todos')

  const lista = useMemo(() => {
    const texto = q.trim().toLowerCase()
    return productos.filter((p) => {
      if (filtro === 'activos' && !p.activo) return false
      if (filtro === 'sinstock' && p.stock !== 0) return false
      if (!texto) return true
      const enNombre = p.nombre.toLowerCase().includes(texto)
      const enInci = p.ingredientes.some((i) => i.nombreInci.toLowerCase().includes(texto))
      return enNombre || enInci
    })
  }, [productos, q, filtro])

  return (
    <>
      <div className="vend-topline">
        <h2>Mis productos</h2>
        <button type="button" className="btn-primario" onClick={onNuevo}>
          ＋ Nuevo producto
        </button>
      </div>

      <div className="filtros-fila">
        <input
          className="buscador"
          placeholder="Buscar por nombre o ingrediente (INCI)"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
        <div className="filtros">
          {(['todos', 'activos', 'sinstock'] as const).map((f) => (
            <button
              key={f}
              type="button"
              className={`filtro ${filtro === f ? 'activo' : ''}`}
              onClick={() => setFiltro(f)}
            >
              {f === 'todos' ? 'Todos' : f === 'activos' ? 'Activos' : 'Sin stock'}
            </button>
          ))}
        </div>
      </div>

      <div className="panel">
        {cargando ? (
          <p className="nota">Cargando…</p>
        ) : lista.length === 0 ? (
          <p className="nota">No hay productos que coincidan.</p>
        ) : (
          <table className="tabla">
            <thead>
              <tr>
                <th>PRODUCTO</th>
                <th>FICHA INCI</th>
                <th>PRECIO</th>
                <th>STOCK</th>
                <th>ESTADO</th>
              </tr>
            </thead>
            <tbody>
              {lista.map((p) => {
                const est = estadoDe(p)
                return (
                  <tr key={p.id} onClick={() => onEditar(p)} className="fila-click">
                    <td className="celda-prod">
                      <span className="thumb">
                        {p.imagenUrl ? <img src={p.imagenUrl} alt={p.nombre} /> : null}
                      </span>
                      {p.nombre}
                    </td>
                    <td>{p.ingredientes.length} ingredientes</td>
                    <td className="precio">{COP.format(p.precio)}</td>
                    <td className={p.stock === 0 ? 'rojo-txt' : ''}>{p.stock}</td>
                    <td>
                      <span className={`badge-estado ${est.cls}`}>{est.txt}</span>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        )}
      </div>
      <p className="nota">Haz clic en un producto para editarlo, gestionar su ficha INCI y publicarlo.</p>
    </>
  )
}

/* ------------------------------------------------- Editor (crear / editar) */
type FichaLocal = IngredienteFicha

function ProductoEditor({
  inicial,
  onGuardado,
  onCambio,
  onVolver,
}: {
  inicial: ProductoVendedor | null
  onGuardado: (p: ProductoVendedor) => void
  onCambio: (p: ProductoVendedor) => void
  onVolver: () => void
}) {
  const esNuevo = inicial === null
  const [prod, setProd] = useState<ProductoVendedor | null>(inicial)
  const [nombre, setNombre] = useState(inicial?.nombre ?? '')
  const [categoria, setCategoria] = useState(inicial?.categoria ?? '')
  const [precio, setPrecio] = useState(String(inicial?.precio ?? ''))
  const [stock, setStock] = useState(String(inicial?.stock ?? '0'))
  const [imagenUrl, setImagenUrl] = useState(inicial?.imagenUrl ?? '')
  const [invima, setInvima] = useState(inicial?.registroInvima ?? '')
  const [fichaLocal, setFichaLocal] = useState<FichaLocal[]>([]) // solo modo crear
  const [inci, setInci] = useState('')
  const [funcion, setFuncion] = useState('')
  const [conc, setConc] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [ocupado, setOcupado] = useState(false)

  const ficha: FichaLocal[] = esNuevo ? fichaLocal : prod?.ingredientes ?? []

  function datos() {
    return {
      nombre: nombre.trim(),
      categoria: categoria.trim() || undefined,
      precio: Number(precio),
      ph: undefined,
      registroInvima: invima.trim() || undefined,
      imagenUrl: imagenUrl.trim() || undefined,
    }
  }

  async function correr<T>(accion: () => Promise<T>, despues?: (r: T) => void) {
    setError(null)
    setOcupado(true)
    try {
      const r = await accion()
      despues?.(r)
    } catch (e) {
      setError(mensajeDeError(e))
    } finally {
      setOcupado(false)
    }
  }

  // Añadir ingrediente: en modo crear se guarda local; en edición va al backend.
  async function anadirInci(e: React.FormEvent) {
    e.preventDefault()
    const nombreInci = inci.trim()
    if (!nombreInci) return
    const c = conc ? Number(conc) : null
    const f = funcion.trim() || null
    if (esNuevo) {
      if (fichaLocal.some((x) => x.nombreInci.toLowerCase() === nombreInci.toLowerCase())) return
      setFichaLocal([...fichaLocal, { nombreInci, funcion: f, concentracion: c }])
      setInci('')
      setFuncion('')
      setConc('')
    } else if (prod) {
      await correr(
        () => agregarIngrediente(prod.id, nombreInci, c, f),
        (p) => {
          setProd(p)
          onCambio(p)
          setInci('')
          setFuncion('')
          setConc('')
        },
      )
    }
  }

  function quitarInci(nombreInci: string) {
    if (esNuevo) {
      setFichaLocal(fichaLocal.filter((x) => x.nombreInci !== nombreInci))
    } else if (prod) {
      correr(() => quitarIngrediente(prod.id, nombreInci), (p) => {
        setProd(p)
        onCambio(p)
      })
    }
  }

  // Crear: POST producto + sus ingredientes; publicar=true añade la publicación.
  async function guardarNuevo(publicar: boolean) {
    await correr(
      async () => {
        let creado = await crearProducto({ ...datos(), ph: null, stock: Number(stock) })
        for (const f of fichaLocal) {
          creado = await agregarIngrediente(creado.id, f.nombreInci, f.concentracion ?? null, f.funcion ?? null)
        }
        if (publicar) creado = await publicarProducto(creado.id)
        return creado
      },
      onGuardado,
    )
  }

  // Editar: guarda datos y, si cambió, el stock.
  async function guardarCambios() {
    if (!prod) return
    await correr(
      async () => {
        let p = await editarProducto(prod.id, { ...datos(), ph: null })
        if (Number(stock) !== prod.stock) p = await actualizarStock(prod.id, Number(stock))
        return p
      },
      (p) => {
        setProd(p)
        onGuardado(p)
      },
    )
  }

  function cambiarPublicacion() {
    if (!prod) return
    correr(() => (prod.activo ? despublicarProducto(prod.id) : publicarProducto(prod.id)), (p) => {
      setProd(p)
      onCambio(p)
    })
  }

  return (
    <>
      <div className="vend-topline">
        <h2>{esNuevo ? 'Nuevo producto' : `Editar: ${prod?.nombre}`}</h2>
        <button type="button" className="btn-sec" onClick={onVolver}>
          ← Volver
        </button>
      </div>

      <div className="editor">
        {/* Columna izquierda: datos comerciales */}
        <section className="panel">
          <h3>Datos comerciales</h3>
          <label className="campo">
            Nombre del producto
            <input value={nombre} onChange={(e) => setNombre(e.target.value)} placeholder="Ej: Serum Vitamina C 10%" maxLength={200} />
          </label>
          <div className="campos-2">
            <label className="campo">
              Categoría
              <input value={categoria} onChange={(e) => setCategoria(e.target.value)} maxLength={120} />
            </label>
            <label className="campo">
              Precio (COP)
              <input type="number" min={0} value={precio} onChange={(e) => setPrecio(e.target.value)} />
            </label>
          </div>
          <div className="campos-2">
            <label className="campo">
              Stock
              <input type="number" min={0} value={stock} onChange={(e) => setStock(e.target.value)} />
            </label>
            <label className="campo">
              Registro INVIMA
              <input value={invima} onChange={(e) => setInvima(e.target.value)} maxLength={100} placeholder="NSOC01234-56CO" />
            </label>
          </div>
          <label className="campo">
            Imagen del producto (URL)
            <input type="url" value={imagenUrl} onChange={(e) => setImagenUrl(e.target.value)} placeholder="https://…" maxLength={500} />
          </label>
          <div className="dropzone">
            {imagenUrl ? <img src={imagenUrl} alt="Vista previa" /> : <span>Pega el enlace de una imagen arriba</span>}
          </div>
        </section>

        {/* Columna derecha: ficha de ingredientes (INCI) */}
        <section className="panel">
          <h3>Ficha de ingredientes (INCI)</h3>
          <table className="inci-tabla">
            <thead>
              <tr>
                <th>INGREDIENTE (INCI)</th>
                <th>FUNCIÓN</th>
                <th>CONC.</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {ficha.length === 0 ? (
                <tr>
                  <td colSpan={4} className="nota">
                    Sin ingredientes. Añade al menos uno para poder publicar.
                  </td>
                </tr>
              ) : (
                ficha.map((i) => (
                  <tr key={i.nombreInci}>
                    <td>{i.nombreInci}</td>
                    <td className="suave">{i.funcion || '—'}</td>
                    <td>{i.concentracion != null ? `${i.concentracion}%` : '—'}</td>
                    <td>
                      <button type="button" className="quitar" aria-label={`Quitar ${i.nombreInci}`} onClick={() => quitarInci(i.nombreInci)}>
                        ×
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>

          <form className="inci-form" onSubmit={anadirInci}>
            <input placeholder="Ingrediente (INCI)" value={inci} onChange={(e) => setInci(e.target.value)} />
            <input placeholder="Función" value={funcion} onChange={(e) => setFuncion(e.target.value)} />
            <input type="number" min={0} max={100} step="0.1" placeholder="%" value={conc} onChange={(e) => setConc(e.target.value)} />
            <button type="submit" className="btn-mini">
              ＋ Agregar activo
            </button>
          </form>

          <p className="nota-amber">⚠️ El sistema mapeará cada ingrediente contra la ontología médica al guardar.</p>
        </section>
      </div>

      {error && <p className="error">{error}</p>}

      <div className="editor-acciones">
        {esNuevo ? (
          <>
            <button type="button" className="btn-sec" disabled={ocupado} onClick={() => guardarNuevo(false)}>
              Guardar borrador
            </button>
            <button type="button" className="btn-primario" disabled={ocupado} onClick={() => guardarNuevo(true)}>
              {ocupado ? 'Guardando…' : 'Publicar producto'}
            </button>
          </>
        ) : (
          <>
            <button type="button" className="btn-sec" disabled={ocupado} onClick={cambiarPublicacion}>
              {prod?.activo ? 'Despublicar' : 'Publicar'}
            </button>
            <button type="button" className="btn-primario" disabled={ocupado} onClick={guardarCambios}>
              {ocupado ? 'Guardando…' : 'Guardar cambios'}
            </button>
          </>
        )}
      </div>
    </>
  )
}

function Placeholder({ titulo }: { titulo: string }): ReactNode {
  return (
    <>
      <div className="vend-topline">
        <h2>{titulo}</h2>
      </div>
      <div className="panel">
        <p className="nota">Esta sección estará disponible al implementar su módulo (pedidos/ventas).</p>
      </div>
    </>
  )
}
