#!/bin/sh
until rc alias set local http://rustfs:9002 ${RUSTFS_ACCESS_KEY} ${RUSTFS_SECRET_KEY}; do sleep 2; done

rc bucket create local/u-announce-media

rc bucket lifecycle rule add local/u-announce-media --prefix 'active/' --expiry-days 30
rc bucket lifecycle rule add local/u-announce-media --prefix 'pending/' --expiry-days 90

exec /opt/rustfs/bin/rustfs-server start