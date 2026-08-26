# Rustfs

[back to README](../README.md)

[RustFS documentation](https://docs.rustfs.com/en)

## Setup

To set up RustFS for uAnnounce you need to create a bucket for it. You can either do it via the RustFS console or via the [rc cli](https://docs.rustfs.com/en/operations/rc). This documentation will use the rc tool.

### Create uAnnounce's bucket

Create the bucket uAnnounce will utilize.

`rc bucket create local/u-announce-media`

### Set lifecycle policies

Set lifecycle policies to ensure that old data doesn't clog up the storage.

`rc bucket lifecycle rule add local/u-announce-media --prefix 'active/' --expiry-days 30`

`rc bucket lifecycle rule add local/u-announce-media --prefix 'pending/' --expiry-days 90`

### folder structure

````
/u-announce-media
    /active
        /org-{orgSlug}-YYYYMMDD_HHmmss-
            /video1.mp4
            /image39.avif
    /pending
        /org-{orgSlug}-YYYYMMDD_HHmmss-
            /unreleased3.mov
            /tobeannounced63.png
````