# Ensure the directory exists
mkdir -p core/shared/src/main/scala/zio/internal/

# Copy your mailbox (el mío) into the right place
cp /path/to/el_mio.scala core/shared/src/main/scala/zio/internal/FiberMailbox.scala
