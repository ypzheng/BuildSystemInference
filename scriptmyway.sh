#/bin/sh


pattern="./"
empty=""
find . -maxdepth 1 -name "*build*.properties" -print | while read line;
do 
file=${line/$pattern/$empty}
if [ -f "$file" ]
then
  echo "$file found."

  while IFS='=' read -r key value
  do
    key=$(echo $key | tr '.' '_')
    eval "${key}='${value}'"
  done < "$file"
  
  cd "$base_dir" | ant "$src_compile"
  echo ""
else
  echo "$file not found"	
fi; done