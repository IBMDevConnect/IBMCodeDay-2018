kubectl delete -f wordpress-deployment.yaml
kubectl delete -f mysql-deployment.yaml
kubectl delete -f local-volumes.yaml
kubectl delete secret mysql-pass
