package ph.txtdis.type;

public enum SyncType {
	BACKUP {
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}, //
	SCRIPT {
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	},
	VERSION {
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	},
	UPDATE {
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	};
}
