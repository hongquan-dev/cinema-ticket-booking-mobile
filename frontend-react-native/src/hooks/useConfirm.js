import { useState } from "react";
import ConfirmDialog from "../components/notification/ConfirmDialog";

export default function useConfirm() {
    const [options, setOptions] = useState(null);

    const confirm = (title, message, type) => {
        return new Promise((resolve) => {
            setOptions({ title, message, type, resolve });
        });
    };

    const handleClose = (result) => {
        if (options) options.resolve(result);
        setOptions(null);
    };

    const ConfirmComponent = options ? (
        <ConfirmDialog
            title={options.title}
            message={options.message}
            type={options.type}
            onClose={handleClose}
        />
    ) : null;

    return { confirm, ConfirmComponent };
}