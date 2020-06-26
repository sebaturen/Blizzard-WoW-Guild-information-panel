#!/usr/bin/python
# xl2tpd | ppp monitoring script
import time
import re
import psutil
import requests
import json

current_connections = {}
ppp_match = re.compile('ppp[0-9]+')
url_api = 'https://artofwar.cl/rest/vpn/'

def poll(interval):
    pnic_before = psutil.net_io_counters(pernic=True)
    time.sleep(interval)
    pnic_after = psutil.net_io_counters(pernic=True)
    return(pnic_before, pnic_after)

def network_ppp_user_ip_monitoring(pnic_before, pnic_after):
    global current_connections, ppp_match, url_api
    current_nics = []
    # network changes
    for nic, addrs in psutil.net_if_addrs().items():
        # check if is ppp[0-9]+
        if ppp_match.match(nic):
            # Append current nic
            current_nics.append(nic)
            # add if not exit
            if nic not in current_connections:
                current_ip = ""
                for addr in addrs:
                    if addr.ptp:
                        current_ip = addr.ptp
                info = {
                    nic: {
                        "ip": current_ip,
                        "start_date": time.time(),
                        "bytes_sent": 0,
                        "bytes_recv": 0,
                        "packets_sent": 0,
                        "packets_recv": 0,
                        "errin": 0,
                        "errout": 0,
                        "dropin": 0,
                        "dropout": 0
                    }
                }
                current_connections.update(info)
            # check change
            network_statistics_before = pnic_before[nic]
            network_statistics_after = pnic_after[nic]
            # calc dif:
            bytes_sent = network_statistics_after.bytes_sent - network_statistics_before.bytes_sent
            bytes_recv = network_statistics_after.bytes_recv - network_statistics_before.bytes_recv
            packets_sent = network_statistics_after.packets_sent - network_statistics_before.packets_sent
            packets_recv = network_statistics_after.packets_recv - network_statistics_before.packets_recv
            errin = network_statistics_after.errin - network_statistics_before.errin
            errout = network_statistics_after.errout - network_statistics_before.errout
            dropin = network_statistics_after.dropin - network_statistics_before.dropin
            dropout = network_statistics_after.dropout - network_statistics_before.dropout
            if (bytes_sent != 0 or bytes_recv != 0 or
                packets_sent != 0 or packets_recv != 0 or
                errin != 0 or errout != 0 or
                dropin != 0 or dropout != 0):
                current_connections[nic]['bytes_sent'] = bytes_sent
                current_connections[nic]['bytes_recv'] = bytes_recv
                current_connections[nic]['packets_sent'] = packets_sent
                current_connections[nic]['packets_recv'] = packets_recv
                current_connections[nic]['errin'] = errin
                current_connections[nic]['errout'] = errout
                current_connections[nic]['dropin'] = dropin
                current_connections[nic]['dropout'] = dropout
                # send API changes?
                requests.post("{}monitoring/{}".format(url_api, current_connections[nic]['ip']), data=json.dumps(current_connections[nic]), verify=False)
                print(current_connections)
    # network is disconnected
    # copy current connections ppp[N]
    current_connect = []
    for c_ppp in current_connections:
        current_connect.append(c_ppp)
    # foreach current connections, and validate if is current nic
    for c_nic in current_connect:
        if c_nic not in current_nics:
            print("{}-[{}] is disconnected".format(c_nic, current_connections[c_nic]['ip']))
            requests.put("{}disconnect/{}".format(url_api, current_connections[c_nic]['ip']), data=json.dumps(current_connections[c_nic]), verify=False)
            del current_connections[c_nic]


if __name__ == "__main__":
    try:
        interval = 0
        while True:
            try:
                args = poll(interval)
                network_ppp_user_ip_monitoring(*args)
                interval = 1
            except:
                print("me cai...")
    except KeyboardInterrupt:
        print("exit!")
