echo 'Separating images'
# get list of .jpg files to work on
shopt -s nullglob
file_list=( *.jpg )

# quick exit if there are no files
[[ ${#file_list[@]} -lt 1 ]] && {
  echo 'No *.jpg files found' >&2
  exit 1
}

# handle each file in the list
for orig_fname in "${file_list[@]}"
do
  # get filename without .xyz suffix
  fname="${orig_fname%.*}"

  # split file name into parts on underscores
  IFS=_ fname_parts=( $fname )

  # vehicle make as subfolder
  printf -v subfolder '%s_' "${fname_parts[@]:0:1}"
  subfolder="${subfolder%_}"  # strip trailing _ from name

  # create subfolder if needed
  mkdir -p "$subfolder"

  # move file to its subfolder
  mv "$orig_fname" "$subfolder"
done
