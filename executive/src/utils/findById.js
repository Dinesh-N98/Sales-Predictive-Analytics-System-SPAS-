export function findById(list, id) {
  return list.find((item) => String(item.id) === String(id));
}