#!/usr/bin/env bash

function killitif {
    docker ps -a  > /tmp/yy_xx$$
    if grep --quiet $1 /tmp/yy_xx$$
     then
     echo "killing older version of $1"
     docker rm -f `docker ps -a | grep $1  | sed -e 's: .*$::'`
   fi
}

function swap_one {
    docker run --network="ecs189_default" --name web1 -d activity
    docker exec ecs189_proxy_1 /bin/bash /bin/swap1.sh
    killitif web2

}

function swap_two {
    docker run --network="ecs189_default" --name web2 -d activity2
    docker exec ecs189_proxy_1 /bin/bash /bin/swap2.sh
    killitif web1

}


if [[ "$1" == "web1" ]]
 then
    echo "Swap one called"
    swap_one
 fi
if [[ "$1" == "web2" ]]
  then
    echo "Swap two called"
    swap_two
fi
