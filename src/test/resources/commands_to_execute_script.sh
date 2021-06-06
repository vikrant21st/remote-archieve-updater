rm -rf "rau_temp_work" && \
mkdir -p "rau_temp_work" && \
cd "rau_temp_work" && \
unzip "../rau_parcel_with_all_data.zip" && \
rm "../rau_parcel_with_all_data.zip" && \
chmod 0775 "rau.sh" && \
./rau.sh && \
cd ".." && \
rm -rf "rau_temp_work"