export interface Producto {
  idProducto: number;
  idCategoria: number;
  nombre: string;
  descripcion: string | null;
  marca: string | null;
  precioVenta: number;
  costoUnitario?: number;
  stock: number;
  imagenUrl: string | null;
  imagenes?: string[];
  estado: string;
}
