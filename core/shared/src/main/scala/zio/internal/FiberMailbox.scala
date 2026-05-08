# Sincroniza la rama de batalla
git checkout -b feature/sha713-mailbox-opt

# Agrega el archivo que acabas de editar
git add core/shared/src/main/scala/zio/internal/FiberMailbox.scala

# Sella el commit
git commit -m "Perf: specialized MPSC FiberMailbox with 4-slot padded fast-path (#8807)"

# Empuja al búnker (GitHub)
git push origin feature/sha713-mailbox-opt
