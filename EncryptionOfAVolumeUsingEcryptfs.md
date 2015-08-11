# Introduction #

> The procedure laid out here is for encrypting a volume a centos box. We follow a "transparent" encryption scheme, meaning, once the volume is mounted on a location, all the applications reading or writing into the location are unaware of the encryption/decryption that is going on at the filesystem level.

# Details #

**The following is the set of steps to follow to encrypt couchdb data for a project named foo on a centos box**

**Install ecryptfs**

> `sudo yum install ecryptfs-utils`

1. Stop services and take a backup.

> service tomcat stop
> service couchdb stop

  * ackup couchdb data.

> mv /var/lib/couchdb /var/lib/couchdb.backup
> mkdir /var/lib/couchdb

**create an independent partition**

1. It is strong recommended that you have an lvm setup. From here on we assume that you are using an lvml

2. lvm create volume for couchdb data.

> lvcreate -L 20G -n foocouch foo

3. mkfs.ext4 /dev/foo/foocouch

4. Modify fstab to for convenient mounts.

vi /etc/fstab

/dev/foo/foocouch  /var/lib/couchdb    ext4        defaults         0 0
/var/lib/couchdb /var/lib/couchdb ecryptfs user,noauto,rw,ecryptfs\_sig=508d8e1251636a58,ecryptfs\_cipher=aes,ecryptfs\_key\_bytes=32,ecryptfs\_fnek\_sig=508d8e1251636a58,ecryptfs\_unlink\_sigs 0 0

5.

> mount /dev/foo/foocouch
> mount -t ecryptfs /var/lib/couchdb

You’ll be prompted to enter a passphrase, this is the passphrase for encrypting/decrypting the volume. (passphrase is used for all future mounts) choose the default aes then choose 2 for 32 byte keys. n on passthrough yes on encrypted filenames.

6. Restore couch data files

> cp  -pr /var/lib/couchdb.backup/**/var/lib/couchdb/**

7. Start application services

> service couchdb start
> service tomcat start