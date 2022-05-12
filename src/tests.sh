make
for i in $(seq -f "%02g" 1 15)
do
    printf "Parsing and testing winzig_%s: " $i
    java winzigc -ast winzig_test_programs/winzig_${i} > tree.${i}
    DIFF=$(diff tree.${i} winzig_test_programs/winzig_${i}.tree)

    if [ "$DIFF" != "" ]
    then
        echo "Failed"
    else
        echo "Successful"
        rm tree.${i}
    fi
done
make clean