(ns pg3.scratch)

(comment

  (start)
  (stop)

  (->
   (init)
   debug)

  (-> (k8s/query cluster-definition)
      (debug))

  (def test-db
    {:kind "Pg"
     :ns "default"
     :apiVersion "pg3.io/v1"
     :metadata {:name "cleo-prod"
                :labels {:service "cleo"
                         :system "pg3"
                         :stage "prod"}}
     :spec {:image "aidbox/aidboxdb"
            :version "passive"
            :size "300Gi"
            :replicas {:sync 1}}
     :config {:config {:shared_buffers "1GB"
                       :max_connections 100}}})

  (-> (k8s/patch test-db)
      (debug))

  (-> (config-map test-db)
      (k8s/patch)
      (debug))

  (def db-secret (secret test-db))

  db-secret

  (-> db-secret
      (k8s/patch)
      (debug))

  (def test-inst-1
    (->
     test-db
     (instance-spec "green" "master")))

  test-inst-1

  (->
   test-inst-1
   (k8s/patch)
   (debug))

  test-inst-1

  (instance-data-volume-spec test-inst-1)

  (instance-data-volume-spec test-inst-1)

  (k8s/patch (instance-data-volume-spec test-inst-1))

  (count (:items (k8s/query (instance-data-volume-spec test-inst-1))))
  

  (create-volumes test-inst-1)

  (-> (k8s/curl "/api/v1")
      (json/parse-string)
      (debug))

  (k8s/delete
   (initdb-pod test-db db-secret test-inst-1))

  (->
   (initdb-pod test-db db-secret test-inst-1)
   ;; (json/generate-string {:pretty true})
   (k8s/create)
   ;; (->> (spit "/tmp/result.yaml"))
   (debug)
   )


  (-> (master-deployment test-db db-secret test-inst-1)
      (k8s/patch)
      (debug))

  (-> (master-service test-db test-inst-1)
      (k8s/patch)
      (debug))


  (def inst-slave
    (->
     test-db
     (instance-spec "blue" "replica")))

  inst-slave

  (k8s/create inst-slave)

  (create-volumes inst-slave)


  (->
   (init-replica-pod test-db db-secret inst-slave)
   (k8s/create)
   (debug))

  #_(k8s/delete
   (init-replica-pod test-db db-secret inst-slave))

  (-> (replica-deployment test-db db-secret inst-slave)
      (k8s/patch)
      (debug))

  (-> (slave-service test-db inst-slave)
      (k8s/patch)
      (debug)
      )

  )
