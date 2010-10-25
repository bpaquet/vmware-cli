# Goals of VMWare CLI

* Do basic operations with virtual machines running into an ESX / ESXi host
    * Start, stop, shutdown guest, reboot guest
    * List virtual machines and get some configuration informations
* Do VM Cloning using a VMWare Converter server
    * Cloning on a single host
    * Cloning from an host to antother

## Why anoher VMWare CLI ?

You can find the original VMWare CLI [here](http://downloads.vmware.com/fr/d/details/vcli40/ZCV0YmRkJSpiKipA).
This CLI does not allow VMWare Converter operation and is based on vmx filename instead of virtual machine names.

# Using VMWare CLI

## Configure

Edit `config.properties` and add a section describing a new esx server :
<code>
esx.my\_server.hostname = hostname.toto.com
esx.my\_server.username = root
esx.my\_server.password = root\_password
</code>

## Basic commands

* List virtual machines on an esx server : `./vmware-cli.sh list my_server`
* Power on virtual machine on an esx server : `./vmware-cli.sh power_on my_server vm_name`
* Power off virtual machine on an esx server : `./vmware-cli.sh power_off my_server vm_name`
* Get details about a virtual machine on an esx server : `./vmware-cli.sh show my_server vm_name`
* Get full command list : `./vmware-cli.sh`

# Installation

## Requirements

* Java 1.6
* Maven 2.x or higher

## Compilation

<code>
git clone git://github.com/bpaquet/vmware-cli.git
cd vmware-cli
mvn clean install
</code>

## Installation

You have two solution :

* use vmware-cli from compilation directory : nothing to do !
* install vmware-cli in another directory :
    * copy the file `target\vmware-cli-1.0-SNAPSHOT-jar-with-dependencies.jar` into the target directory
    * copy the file `vmware-cli.sh` into the target directory, and adapt paths into the file


	
