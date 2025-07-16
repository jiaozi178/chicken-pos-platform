const baseURL = 'http://localhost:8081'
export function getImageUrl(pic: string) {
  if (!pic) return ''
  if (pic.startsWith('http')) return pic
  return baseURL + pic
}